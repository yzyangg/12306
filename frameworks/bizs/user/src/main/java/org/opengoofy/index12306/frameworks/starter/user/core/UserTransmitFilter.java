/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opengoofy.index12306.frameworks.starter.user.core;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.opengoofy.index12306.framework.starter.bases.constant.UserConstant;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 用户信息传输过滤器
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：12306）获取项目资料
 */
public class UserTransmitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 获取用户信息
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String userId = httpServletRequest.getHeader(UserConstant.USER_ID_KEY);


        if (StringUtils.hasText(userId)) {
            // userName
            String userName = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
            // realName
            String realName = httpServletRequest.getHeader(UserConstant.REAL_NAME_KEY);

            // URL 解码
            if (StringUtils.hasText(userName)) {
                userName = URLDecoder.decode(userName, UTF_8);
            }
            if (StringUtils.hasText(realName)) {
                realName = URLDecoder.decode(realName, UTF_8);
            }

            // 获取 token
            String token = httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);


            // 构建用户信息
            UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .username(userName)
                    .realName(realName)
                    .token(token)
                    .build();

            // 传递用户信息
            UserContext.setUser(userInfoDTO);
        }
        try {
            // 执行过滤器
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 执行完后清空用户信息
            UserContext.removeUser();
        }
    }
}
