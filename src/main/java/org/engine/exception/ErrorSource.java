package org.engine.exception;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ErrorSource {

    private String pointer;

    private String parameter;
    
    public ErrorSource(String parameter, String pointer){
    	this.parameter = StringUtils.isNoneBlank(parameter) ? parameter : null;
    	this.pointer = StringUtils.isNoneBlank(pointer) ? pointer : null;
    }
}
