package touchsoft.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import touchsoft.model.Role;

public class UserDataDTO {
  
  @ApiModelProperty(position = 0)
  private String username;

  @ApiModelProperty(position = 1)
  private String password;
  @ApiModelProperty(position = 2)
  List<Role> roles;
  public UserDataDTO() {
  }

  public UserDataDTO(String username, String password, List<Role> roles) {
    this.username = username;
    this.password = password;
    this.roles = roles;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

}
