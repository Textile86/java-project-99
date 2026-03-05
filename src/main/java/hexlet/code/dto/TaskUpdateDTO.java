package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {

    @Size(min = 1)
    @JsonProperty("title")
    private JsonNullable<String> name = JsonNullable.undefined();

    private JsonNullable<Integer> index = JsonNullable.undefined();

    private JsonNullable<Set<Long>> taskLabelIds = JsonNullable.undefined();

    @JsonProperty("content")
    private JsonNullable<String> description = JsonNullable.undefined();

    private JsonNullable<String> status = JsonNullable.undefined();

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId = JsonNullable.undefined();
}
