# Auth Service

This app is a service created to support authentication and authorization features of any app. The current mode of authentication is JWT authentication.

## Features
1) User sign on
2) User sign in
4) Each User supports 3 roles - Admin, User, Member

## Flow of the code

We have used different configurations and filters to achieve the functionality. The following happens - 
1) When the app starts, the SecurityConfig is the first thing to run.
2) When a request is sent to any endpoint, it first reaches the JwtFilter, where the Authorization and Token, if any, are validated.
3) In case of any errors at this stage, the exception handling in JwtAuthenticationEntryPoint takes place.
4) If the auth is successful, then the request finally reaches the respective controller.
