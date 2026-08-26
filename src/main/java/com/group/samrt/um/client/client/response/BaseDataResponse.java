package com.group.samrt.um.client.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
public class BaseDataResponse<T> {
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("responseMessage")
    private String responseMessage;
    @JsonProperty("body")
    private T body;

    public BaseDataResponse(T response) {
    }

    public String getResponseCode() {
        return this.responseCode;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public T getBody() {
        return this.body;
    }

    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("responseMessage")
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @JsonProperty("body")
    public void setBody(T body) {
        this.body = body;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseDataResponse)) {
            return false;
        } else {
            BaseDataResponse<?> other = (BaseDataResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$responseCode = this.getResponseCode();
                    Object other$responseCode = other.getResponseCode();
                    if (this$responseCode == null) {
                        if (other$responseCode == null) {
                            break label59;
                        }
                    } else if (this$responseCode.equals(other$responseCode)) {
                        break label59;
                    }

                    return false;
                }

                Object this$responseMessage = this.getResponseMessage();
                Object other$responseMessage = other.getResponseMessage();
                if (this$responseMessage == null) {
                    if (other$responseMessage != null) {
                        return false;
                    }
                } else if (!this$responseMessage.equals(other$responseMessage)) {
                    return false;
                }
                Object this$body = this.getBody();
                Object other$body = other.getBody();
                if (this$body == null) {
                    if (other$body != null) {
                        return false;
                    }
                } else if (!this$body.equals(other$body)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseDataResponse;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $responseCode = this.getResponseCode();
        result = result * 59 + ($responseCode == null ? 43 : $responseCode.hashCode());
        Object $responseMessage = this.getResponseMessage();
        result = result * 59 + ($responseMessage == null ? 43 : $responseMessage.hashCode());
        Object $body = this.getBody();
        result = result * 59 + ($body == null ? 43 : $body.hashCode());
        return result;
    }

    public String toString() {
        return "BaseDataResponse(responseCode=" + this.getResponseCode() + ", responseMessage=" + this.getResponseMessage() + ", body=" + this.getBody() + ")";
    }

    public BaseDataResponse(String responseCode, T body) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.body = body;
    }

    public BaseDataResponse() {
    }
}
