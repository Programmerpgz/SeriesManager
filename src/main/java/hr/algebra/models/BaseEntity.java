package hr.algebra.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;

public abstract class BaseEntity implements Serializable {
    @JsonIgnore
    private int id;
    protected BaseEntity(int id) {
        this.id = id;
    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseEntity that)){
            return false;
        }
        if (getClass() != o.getClass()){
            return false;
        }
        return id != 0 && id == that.id;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public abstract String toString();
}