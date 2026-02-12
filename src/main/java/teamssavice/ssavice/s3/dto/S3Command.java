package teamssavice.ssavice.s3.dto;

import java.util.List;
import lombok.Builder;

public class S3Command {

    @Builder
    public record ValidateKeys(
        List<String> objectKeys
    ) {

    }

}
