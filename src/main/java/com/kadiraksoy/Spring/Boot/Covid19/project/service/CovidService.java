package com.kadiraksoy.Spring.Boot.Covid19.project.service;

import com.kadiraksoy.Spring.Boot.Covid19.project.model.LocationStats;
import jakarta.annotation.PostConstruct;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";


    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    // belirli bir bean (nesne) oluşturulduktan hemen sonra çalıştırılacak bir metodu belirtmek için kullanılır
    @PostConstruct
    // belirli bir zamanlama planına göre düzenli olarak çalıştırmak için kullanılır
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException{
        List<LocationStats> newStats = new ArrayList<>();
        //HttpClient sınıfından bir örnek oluşturarak HTTP isteklerini göndermek için bir istemci oluşturur.
        // Bu istemci, belirli bir URL'ye istek yapmak ve yanıtı almak için kullanılabilir.
        // Oluşturulan HttpClient örneği, HttpRequest nesneleri üzerinden HTTP isteklerini yapar
        // ve HttpResponse nesneleri ile yanıtları alır.
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // HTTP yanıtının içeriği bir StringReader nesnesine aktarılır.
        //üzerindeki CSV verileri ayrıştırılır.
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        //nesneleri oluşturarak istatistiklerin işlenmesini gerçekleştirir
        // ve newStats listesine ekler. Son olarak, allStats listesi newStats listesiyle güncellenir.
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
            newStats.add(locationStat);
        }
        this.allStats = newStats;




    }

}
