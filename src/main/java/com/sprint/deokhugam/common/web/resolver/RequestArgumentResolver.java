package com.sprint.deokhugam.common.web.resolver;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.common.exception.MissingHeaderException;
import com.sprint.deokhugam.common.web.annotation.RequestHeader;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class RequestArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(RequestHeader.class);
  }

  @Override
  public @Nullable Object resolveArgument(
      MethodParameter parameter,
      @Nullable ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      @Nullable WebDataBinderFactory binderFactory) {
    RequestHeader parameterAnnotation = parameter.getParameterAnnotation(RequestHeader.class);
    String headerName = parameterAnnotation.value();
    String headerValue = webRequest.getHeader(headerName);
    if (headerValue == null) {
      throw new MissingHeaderException(headerName);
    }
    WebDataBinder binder;
    try {
      binder = binderFactory.createBinder(webRequest, null, headerName);
    } catch (Exception e) {
      throw new IllegalStateException("binder 생성 실패.", e);
    }
    try {
      Object result =
          binder.convertIfNecessary(headerValue, parameter.getParameterType(), parameter);
      if (result == null) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }
      return result;
    } catch (ConversionNotSupportedException exception) {
      throw exception;
    } catch (TypeMismatchException exception) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, exception);
    }
  }
}
