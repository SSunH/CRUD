//package Sun.crud.utils;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.RequestEntity;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//
//public class OAuth2RestClient {
//   
//
//    public ResponseEntity<String> getResource(String resourceServerUrl, String accessToken) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<String> responseEntity = restTemplate.exchange(
//                    resourceServerUrl,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//            return responseEntity;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("리소스 서버 요청 실패: " + e.getMessage());
//        }
//    }
//}