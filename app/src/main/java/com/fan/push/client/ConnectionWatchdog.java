/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fan.push.client;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import static com.fan.push.client.ServerConfig.SERVER_IP;
import static com.fan.push.client.ServerConfig.SERVER_PORT;
import static com.fan.push.util.LoggerUtil.logger;


public class ConnectionWatchdog extends ChannelInboundHandlerAdapter {

    private String remoteAddress = SERVER_IP+":"+SERVER_PORT;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();

        // 清零
        PushClient.getInstance().clearAttempts();

        logger.info("Connects with {}.", ch);

        ctx.fireChannelActive();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        boolean doReconnect = PushClient.getInstance().isReconnectNeeded();
        // 需要重连
        if (doReconnect) {
           PushClient.getInstance().startNewTimerToReconnect();
        }

        logger.warn("Disconnects with {}, address: {}, reconnect: {}.", ctx.channel(), remoteAddress, doReconnect);

        ctx.fireChannelInactive();
    }
}
