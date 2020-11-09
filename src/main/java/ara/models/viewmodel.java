package ara.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
public class viewmodel {
    @NotNull
    @Size(min=5,message=" min 5 char at least")
    private  String namex;


}
