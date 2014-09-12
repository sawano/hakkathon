/*
 * Copyright 2014 Daniel Sawano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.sawano.akka.chicken.web;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import se.sawano.akka.chicken.model.racecourse.DriverStats;
import se.sawano.akka.chicken.server.ServerApp;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.springframework.boot.SpringApplication.run;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
@EnableAutoConfiguration
public class ScoreBoardController {

    private ServerApp serverApp;

    @RequestMapping("/")
    public String index(final ModelMap modelMap) {
        modelMap.addAttribute("allDrivers", serverApp.scores());
        return "index";
    }

    @RequestMapping(value = "/results", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Stat> results() {
        final List<DriverStats> scores = serverApp.scores();
        return scores.stream().map(s -> new Stat(s.driverId().alias(), s.score())).collect(toList());
    }

    @RequestMapping(value = "/reset", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> resetScores() {
        serverApp.resetScores();
        return singletonMap("message", "Scores has been reset");
    }

    @PostConstruct
    public void init() {
        serverApp = new ServerApp().start();
    }

    @PreDestroy
    public void destroy() {
        serverApp.stop();
    }

    /**
     * Starts the web application.
     */
    public static void main(String[] args) throws Exception {
        run(ScoreBoardController.class, args);
    }

    public static class Stat {
        private final String alias;
        private final long score;

        public Stat(final String alias, final long score) {
            this.alias = alias;
            this.score = score;
        }

        public String getAlias() {
            return alias;
        }

        public long getScore() {
            return score;
        }

    }

}
