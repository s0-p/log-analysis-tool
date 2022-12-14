package com.gwd.tracetool.service;

import com.gwd.tracetool.component.ToolProperties;
import com.gwd.tracetool.domain.ApiModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.gwd.tracetool.utils.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiParserServiceImpl implements ApiParserService {

    private final ToolProperties toolProperties;

    public List<ApiModel> readApiList(String date) {
        int dagsHost = 1;
        List<ApiModel> list = new ArrayList<ApiModel>();
        String fileName = generateLogfileName(date);

        List<Path> dagsLogDirPathList = new ArrayList<>(Arrays.asList(
                Paths.get(toolProperties.getDags1LogPath(), fileName),
                Paths.get(toolProperties.getDags2LogPath(), fileName)));

        for (Path logPath : dagsLogDirPathList) {
            list.addAll(readApiLogFile(logPath, dagsHost++));
        }

        Collections.sort(list);
        return list;
    }

    private List<ApiModel> readApiLogFile(Path path, int dagsHost) {
        List<ApiModel> subList = new ArrayList<ApiModel>();
        File file = new File(path.toString());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.contains(API_QUEUE_RECORD)) {
                    continue;
                }

                int i = line.indexOf(API_QUEUE_RECORD) + API_QUEUE_RECORD.length();
                if (i != -1) {

                    LocalDateTime occurrenceTime = LocalDateTime.parse(line.substring(0, 23), FORMATTER);
                    String strDate = line.substring(i);

                    ApiModel apiModel = createApiModel(strDate);
                    apiModel.setOccurrenceTime(occurrenceTime);
                    apiModel.setDagsHost(dagsHost);
                    subList.add(apiModel);
                }
            }
        } catch (FileNotFoundException e) {
            log.info("File Dose Not Exist : log-path={}, stack-trace={}", path, new Throwable().getStackTrace());
        } catch (IOException e) {
            log.info("Fail to parse log : log-path={}, stack-trace={}", path, new Throwable().getStackTrace());
        }
        return subList;
    }

    private ApiModel createApiModel(String strData) {
        List<String> urlList = List.of(strData.split(", code:"));
        List<String> statusList = List.of(urlList.get(1).split(",|:"));

        urlList = List.of(urlList.get(0).split(":"));

        String apiType = urlList.get(3).substring(4);
        if (apiType.contains("OPERATION")) {
            int index = apiType.indexOf("/OPERATION");
            apiType = apiType.substring(0, index);
        }
        return ApiModel.builder()
                .host(urlList.get(2).substring(2))
                .apiType(apiType)
                .code(Integer.valueOf(statusList.get(0)))
                .time(statusList.get(2))
                .message(statusList.get(4))
                .body(statusList.get(6))
                .build();
    }

    private String generateLogfileName(String date) {
        // example : dags_feign.2022-07-14.log
        SIMPLE_FORMATTER.setLenient(false);
        try {
            // ?????? ?????? ??????
            SIMPLE_FORMATTER.parse(date);
        } catch (java.text.ParseException e) {
            log.info("Fail to parse log : log-path={}, stack-trace={}", date, new Throwable().getStackTrace());
        }
        return String.format("dags_feign.%s.log", date);
    }


}
