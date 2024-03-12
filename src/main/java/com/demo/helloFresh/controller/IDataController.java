package com.demo.helloFresh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RequestMapping("/action")
public interface IDataController {
    @PostMapping(value = "/event")
    @Operation(summary = "Post event related data")
    @ApiResponses(value = {@ApiResponse(responseCode = "202", description = "Success")})
    String uploadData( @RequestParam(value = "dataToUpload") @Valid @NotNull @NotBlank List<String> dataToUpload) throws IOException;

    @GetMapping("/stats")
    @Operation(summary = "Get status of events happened in past 60 seconds")
    @ApiResponses(value = {@ApiResponse(responseCode = "202", description = "Success")})
    String getEventStatus();

}
