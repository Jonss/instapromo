package br.com.instapromo.instapromo.model;

import java.util.List;

public class FourSquareResponse {

    Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public List<Venue> getVenues() {
        return response.getVenues();
    }

    public class Response {
        List<Venue> venues;

        public List<Venue> getVenues() {
            return venues;
        }

        public void setVenues(List<Venue> venues) {
            this.venues = venues;
        }
    }
}