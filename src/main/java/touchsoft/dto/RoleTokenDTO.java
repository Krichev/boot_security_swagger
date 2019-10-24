package touchsoft.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;
import touchsoft.model.Role;

import java.util.List;

@Data
public class RoleTokenDTO {
    private String role;

    private String token;

    public RoleTokenDTO() {
    }

    public RoleTokenDTO(String role, String token) {
        this.role = role;
        this.token = token;
    }
}
