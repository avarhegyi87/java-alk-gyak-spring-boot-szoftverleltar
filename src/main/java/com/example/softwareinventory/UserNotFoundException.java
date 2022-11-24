package com.example.softwareinventory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    UserNotFoundException(Long id) {
        super("Nem tartozik felhasználó a megadott azonosítóhoz: " + id);
    }

}
