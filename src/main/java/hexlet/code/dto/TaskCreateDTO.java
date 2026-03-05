package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank
    @Size(min = 1)
    @JsonProperty("title")
    private String name;

    private Integer index;

    private Set<Long> taskLabelIds = new HashSet<>();

    @JsonProperty("content")
    private String description;

    @NotNull
    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
}
