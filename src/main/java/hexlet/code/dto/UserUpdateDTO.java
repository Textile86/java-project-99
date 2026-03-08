package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {

    private JsonNullable<String> firstName = JsonNullable.undefined();
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @Email(message = "Email должен быть валидным")
    private JsonNullable<String> email = JsonNullable.undefined();

    @Size(min = 3, message = "Пароль должен быть минимум 3 символа")
    private JsonNullable<String> password = JsonNullable.undefined();
}
