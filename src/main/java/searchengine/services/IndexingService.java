package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import searchengine.builder.LinkParser;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.repositories.SiteEntityRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;


@Service
public class IndexingService {

    @Autowired
    SiteEntityRepository siteEntityRepository;

    @Autowired
    private final SitesList sites;

    public IndexingService(SitesList sites) {
        this.sites = sites;
    }

    public HttpStatus startIndexing() {

        List<SiteEntity> siteEntityList = new ArrayList<>();

        for (Site site : sites.getSites()) {
            String url = site.getUrl();
            String name = site.getName();

            SiteEntity siteEntity = new SiteEntity(Status.INDEXING, LocalDateTime.now(), "", url, name);
            siteEntityList.add(siteEntity);
        }


        //Сохранение сайта в базу данных и запуск его парсинга (для всех доступных сайтов)
        for (SiteEntity it : siteEntityList) {

            siteEntityRepository.saveAndFlush(it);


             LinkParser linkParser = new LinkParser(it.getUrl());
             ForkJoinPool pool = new ForkJoinPool();
             pool.invoke(linkParser);

        }
        return HttpStatus.OK;
    }

    /**
     boolean isIndexing = pool.isTerminating();
     if (isIndexing) {
     return new ResponseEntity<>(HttpStatus.IM_USED);
     } else {
     pool.invoke(linkParser);
     return new ResponseEntity<>(HttpStatus.OK);
     }
     */
}
