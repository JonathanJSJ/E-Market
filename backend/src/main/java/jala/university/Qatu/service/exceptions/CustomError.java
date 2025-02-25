package jala.university.Qatu.service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomError {
    private String message;
}