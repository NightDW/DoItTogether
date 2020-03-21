package com.laidw.web.plugin.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 高版本的SpringSecurity要求我们提供密码解析器，简单实现一个即可
 */
@Component
public class PasswordEncoderImpl implements PasswordEncoder {

    public String encode(CharSequence charSequence) {
        return charSequence.toString();
    }

    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(charSequence.toString());
    }
}
