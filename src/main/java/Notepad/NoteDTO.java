package Notepad;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteDTO {

    @NotEmpty(message = "The name is required")
    String name;

    @Size(max = 100000, message = "The text cannot exceed 10000 characters.")
    String text;

}
