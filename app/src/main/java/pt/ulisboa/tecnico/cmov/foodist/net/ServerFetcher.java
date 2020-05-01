package pt.ulisboa.tecnico.cmov.foodist.net;

public class ServerFetcher {
    private final String baseUrl = "https://data.florianmornet.fr/api";

    public String fetchCafeterias() {
        String urlString = baseUrl + "/cafeterias";
        return NetUtils.download(urlString);
    }

    public String fetchCafeteria(int cafeteriaId) {
        String urlString = baseUrl + "/cafeterias/" + cafeteriaId;
        return NetUtils.download(urlString);
    }
}
