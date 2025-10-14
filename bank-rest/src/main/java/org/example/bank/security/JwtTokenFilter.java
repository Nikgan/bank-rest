package org.example.bank.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.bank.model.User;
import org.example.bank.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println("JwtTokenFilter: Authorization header = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println("JwtTokenFilter: token = " + token);

            try {
                boolean valid = jwtTokenProvider.validateToken(token);
                System.out.println("JwtTokenFilter: token valid = " + valid);

                if (!valid) {
                    System.out.println("JwtTokenFilter: токен невалидный");
                } else {
                    String username = jwtTokenProvider.getUsername(token);
                    System.out.println("JwtTokenFilter: extracted username = " + username);

                    Optional<User> optionalUser = userRepository.findByUsername(username);
                    if (optionalUser.isEmpty()) {
                        System.out.println("JwtTokenFilter: User not found in DB: " + username);
                    } else {
                        User user = optionalUser.get();
                        System.out.println("JwtTokenFilter: User found in DB: " + username + ", roles = " + user.getRoles());

                        UserPrincipal principal = new UserPrincipal(
                                user.getId(),
                                user.getUsername(),
                                user.getPasswordHash(),
                                user.getRoles().stream()
                                        .map(role -> (GrantedAuthority) () -> "ROLE_" + role.getName())
                                        .toList()
                        );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(auth);
                        System.out.println("JwtTokenFilter: User set in SecurityContext");
                    }
                }
            } catch (Exception e) {
                System.out.println("JwtTokenFilter: ошибка при обработке токена");
                e.printStackTrace();
            }
        } else {
            System.out.println("JwtTokenFilter: Authorization header отсутствует или не Bearer");
        }

        filterChain.doFilter(request, response);
    }
}
