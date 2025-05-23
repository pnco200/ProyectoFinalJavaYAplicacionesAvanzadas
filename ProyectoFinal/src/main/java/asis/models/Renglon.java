package asis.models;
import jakarta.persistence.*;
@Entity
public class Renglon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) //No tiene sentido crear el renglon si el campo esta vacio directamente no se crea, no es que se crea y lo dejamos nulo
    private String valor;

    @Column(nullable = false)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "encuesta_id")
    private Encuesta encuesta;

    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public Long getId() {
		return id;
	}

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Encuesta getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(Encuesta encuesta) {
        this.encuesta = encuesta;
    }

    
}