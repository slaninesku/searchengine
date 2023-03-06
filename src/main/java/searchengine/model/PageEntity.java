package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "page")
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @JoinColumn(name = "site_id", nullable = false)
    @ManyToOne
    private SiteEntity siteEntity;

    @Column(columnDefinition = "TEXT", nullable = false)
    @PrimaryKeyJoinColumn
    private String path;

    @Column(nullable = false)
    private int code;

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

    @Override
    public String toString() {
        return "model.Page{" +
                "\tid=" + id + "" +
                "\tsiteId=" + siteEntity + "" +
                "\tpath='" + path + '\'' + "" +
                "\tcode=" + code + "" +
                "\tcontent=" + content + "" +
                '}';
    }

}