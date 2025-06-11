package dragor.com.webapp.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import dragor.com.webapp.util.StatusConverter;
import dragor.com.webapp.util.TypeConverter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import jdk.jshell.Snippet;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tid;

    private Double amount;

    private Double tfees;


    private String sender;

    private String receiver;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Convert(converter = StatusConverter.class)
    private Status status;
    @Convert(converter = TypeConverter.class)
    private Type type;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_id")
    private Account account;


}
