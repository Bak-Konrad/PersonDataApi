package kb.persondata.person.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Version
    private Long version;

    @NotBlank
    private String entityType;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @Pattern(regexp = "\\d{11}", message = "Personal number must have exactly 11 digits")
    @Column(unique = true)
    private String personalNumber;

    @NotNull(message = "Height cannot be null")
    @DecimalMin(value = "30.0", message = "Height must be greater than or equal to 30.0 (cm)")
    @DecimalMax(value = "280.0", message = "Height cannot exceed 272.0 (cm)")
    private Double height;

    @NotNull(message = "Weight cannot be null")
    @DecimalMin(value = "0.30", message = "Weight must be greater than or equal to 0.3 (kg)")
    @DecimalMax(value = "635.0", message = "Weight cannot exceed 635.0 (kg)")
    private Double weight;

    @NotBlank(message = "Email address cannot be blank")
    @Email(message = "Invalid email address")
    @Size(max = 50, message = "Email address must be less than or equal to 50 characters")
    @Column(unique = true)
    private String emailAddress;
}
