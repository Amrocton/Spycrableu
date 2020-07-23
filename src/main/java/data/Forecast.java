package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class Forecast {
    public String response;
    private double lat;
    private double lon;
    public Forecast(Double[] city_coords){
        this.lat = Double.parseDouble(Array.get(city_coords, 0).toString());
        this.lon = Double.parseDouble(Array.get(city_coords, 1).toString());
    }
    public void Weather() throws IOException {
        /* Убрать потом ниже, чтобы работало с геокодером*/
        this.lat = 55.753215;
        this.lon = 37.622504;
        /* Вот до сюды*/
        URL url = new URL(String.format(Locale.US, "https://api.weather.yandex.ru/v2/forecast?lat=%.2f&lon=%.2f&lang=ru_RU", this.lat, this.lon));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("X-Yandex-API-Key", "5b90c506-adad-4717-a2f3-70c4232bdfe6");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        response = new String(content);
        in.close();
        con.disconnect();

    }
}
