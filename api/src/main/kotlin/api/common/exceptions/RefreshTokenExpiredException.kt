package api.common.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.FORBIDDEN)
class RefreshTokenExpiredException : Exception("Refresh token is expired. Please make a new sign in request")