package ru.unio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class InterestLoader {

    private List<String> interests;

    public InterestLoader() {
        loadInterests();
    }

    private void loadInterests() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getResourceAsStream("/json/interests.json")) {
            InterestsWrapper wrapper = objectMapper.readValue(inputStream, InterestsWrapper.class);
            this.interests = wrapper.getInterests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getInterests() {
        return interests;
    }

    private static class InterestsWrapper {
        private List<String> interests;

        public List<String> getInterests() {
            return interests;
        }

        public void setInterests(List<String> interests) {
            this.interests = interests;
        }
    }
}
