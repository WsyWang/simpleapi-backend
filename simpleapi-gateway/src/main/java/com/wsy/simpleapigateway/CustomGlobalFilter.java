package com.wsy.simpleapigateway;

import com.wsy.simpleapiclientsdk.utils.SignUtils;
import com.wsy.simpleapicommon.model.entity.InterfaceInfo;
import com.wsy.simpleapicommon.model.entity.User;
import com.wsy.simpleapicommon.service.InnerInterfaceInfoService;
import com.wsy.simpleapicommon.service.InnerUserInterfaceInfoService;
import com.wsy.simpleapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 全局过滤器
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    public static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //用户发送请求到 API 网关
        //请求日志
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求唯一标识:" + request.getId());
        String path = request.getPath().value();
        log.info("请求路径:" + path);
        String methodValue = request.getMethodValue();
        log.info("请求方法:" + methodValue);
        log.info("请求参数:" + request.getQueryParams());
        log.info("请求来源地址:" + request.getRemoteAddress());
        String hostString = request.getLocalAddress().getHostString();
        log.info("请求来源地址:" + hostString);
        //（黑白名单）
        ServerHttpResponse response = exchange.getResponse();
        if (!IP_WHITE_LIST.contains(hostString)) {
            return handleNoAuth(response);
        }
        //用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        //判断是否分配给用户密钥
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if (invokeUser == null) {
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }

        //和当前时间不能超过五分钟
        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        //从数据库中查出 secretKey
        //服务端进行签名验证
        String secretKey = invokeUser.getSecretKey();
        String serverSign;
        if (body.equals("")) {
            serverSign = SignUtils.genSign(null, secretKey);
        } else {
            serverSign = SignUtils.genSign(body, secretKey);
        }
        if (!serverSign.equals(sign)) {
            return handleNoAuth(response);
        }
        //请求的模拟接口是否存在？
        //从数据库中查询接口是否存在
        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, methodValue);
        if (interfaceInfo == null) {
            return handleInvokeError(response);
        }
        //查询是否还有调用次数
        if (!innerUserInterfaceInfoService.invokeCountOver(interfaceInfo.getId(), invokeUser.getId())) {
          return handleNoAuth(response);
        }
        //查询是否被封号
        if (innerUserInterfaceInfoService.isBan(interfaceInfo.getId(), invokeUser.getId())) {
            return handleNoAuth(response);
        }

        //请求转发，调用模拟接口，并打印响应日志
        return handleLog(exchange, chain, interfaceInfo.getId(), invokeUser.getId());

    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    public Mono<Void> handleLog(ServerWebExchange exchange, GatewayFilterChain chain, Long interfaceInfoId, Long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //调用成功，接口调用次数 + 1
                            try {
                                innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                            } catch (Exception e) {
                                log.error("invokeCount error", e);
                            }
                            return super.writeWith(fluxBody.map(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 构建返回日志
                                String data = new String(content, StandardCharsets.UTF_8);
                                log.info("响应结果：" + data);
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            //调用失败，返回一个规范的错误码
                            log.error("<-- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}