package touchsoft.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()//
                .antMatchers("/touch/login").permitAll()//
                .antMatchers("/touch/register").permitAll()//
                .antMatchers("/web/login").permitAll()//
                .antMatchers("/web/register").permitAll()//
                /////////////////////////
                .antMatchers("/login").permitAll()
                .antMatchers("/chat/**").permitAll()
                .antMatchers("/chat").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/*.js").permitAll()
                .antMatchers("/ **/*.html").permitAll()
                .antMatchers("/ **/*.js").permitAll()
                .antMatchers("/ **/*.css").permitAll()

                .anyRequest().authenticated()

                .and().logout().logoutUrl("/login?logout");//adding for login page

        http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs")//
                .antMatchers("/swagger-resources/**")//
                .antMatchers("/swagger-ui.html")//
                .antMatchers("/configuration/**")//
                .antMatchers("/webjars/**")//
                .antMatchers("/public")

                // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
                .and()
                .ignoring();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
