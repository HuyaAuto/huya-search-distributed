package com.huya.search.restful.http.handler;

import com.huya.search.restful.http.HttpReceiverContext;
import com.huya.search.restful.http.HttpRequest;
import com.huya.search.restful.http.HttpResponse;
import com.huya.search.restful.http.HttpRouter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@ChannelHandler.Sharable
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger LOG = LoggerFactory.getLogger(HttpDispatcherHandler.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
		
		FullHttpRequest httpReq = (FullHttpRequest) msg;
		HttpReceiverContext context = new HttpReceiverContext(new HttpRequest(httpReq, ctx), new HttpResponse(httpReq));

		try {
			HttpRouter.receive(context);
		} catch (Exception e) {
			context.httpResponse().setContent("server exception".getBytes());
			context.httpResponse().setStatus(HttpResponse.INTERNAL_SERVER_ERROR);
			LOG.error("http exception happened", e);
		}

		FullHttpResponse fullHttpResponse = context.httpResponse().nativeHttpResponse();
		ChannelFuture future = ctx.write(fullHttpResponse);
		if (!context.httpResponse().isKeepAlive()) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		ByteBuf buf = Unpooled.copiedBuffer("server inner err".getBytes());
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, buf);
		ctx.writeAndFlush(fullHttpResponse);
		ctx.channel().close();
		
		if (!(cause instanceof IOException))
			LOG.error("http netty handler exception occurred", cause);
	}

}