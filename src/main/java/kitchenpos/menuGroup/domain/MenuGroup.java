package kitchenpos.menuGroup.domain;

import kitchenpos.common.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MenuGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    public MenuGroup() {
    }

    public MenuGroup(String name) {
        this.name = name;
    }

    public MenuGroup(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MenuGroup createMenuGroup(final String name) {
        return new MenuGroup(name);
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
