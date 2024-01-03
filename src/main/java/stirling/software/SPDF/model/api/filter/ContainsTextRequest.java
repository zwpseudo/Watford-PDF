package watford.software.SPDF.model.api.filter;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import watford.software.SPDF.model.api.PDFWithPageNums;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContainsTextRequest extends PDFWithPageNums {

    @Schema(description = "The text to check for", required = true)
    private String text;
}
