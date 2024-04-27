package Notepad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SearchingObject {
    
    @NotBlank(message = "Search is blank!")
    String text;

    String type;

}
