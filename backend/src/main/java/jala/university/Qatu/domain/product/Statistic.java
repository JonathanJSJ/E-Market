package jala.university.Qatu.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Table(name = "statistics")
@Entity(name = "statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigInteger visits = BigInteger.valueOf(0);

    @OneToMany(mappedBy = "statistic", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Product> products;

    public void addVisit(){
        visits = visits.add(BigInteger.ONE);
    }
}
