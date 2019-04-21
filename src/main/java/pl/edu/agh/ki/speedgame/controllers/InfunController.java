package pl.edu.agh.ki.speedgame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.edu.agh.ki.speedgame.exceptions.CannotRemoveGameException;
import pl.edu.agh.ki.speedgame.exceptions.GameWithSuchIdExistException;
import pl.edu.agh.ki.speedgame.exceptions.LackOfAccessException;
import pl.edu.agh.ki.speedgame.exceptions.NoGameSelectedException;
import pl.edu.agh.ki.speedgame.exceptions.NoMoreAvailableTasksException;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchGameException;
import pl.edu.agh.ki.speedgame.exceptions.NoSuchUserException;
import pl.edu.agh.ki.speedgame.exceptions.SuchUserExistException;
import pl.edu.agh.ki.speedgame.model.Game;
import pl.edu.agh.ki.speedgame.model.domain.TaskResult;
import pl.edu.agh.ki.speedgame.model.requests.CreateGameInput;
import pl.edu.agh.ki.speedgame.model.requests.CreateGameInputConfig;
import pl.edu.agh.ki.speedgame.model.requests.JoinGameInput;
import pl.edu.agh.ki.speedgame.model.requests.LastResultResponse;
import pl.edu.agh.ki.speedgame.model.requests.TaskMark;
import pl.edu.agh.ki.speedgame.services.IFolderScanService;
import pl.edu.agh.ki.speedgame.services.IGameService;
import pl.edu.agh.ki.speedgame.services.MarkService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
//todo hide all functions body to service
public class InfunController {

    @Autowired
    IGameService gameService;

    @Autowired
    IFolderScanService folderScanService;

    @Autowired
    MarkService markService;

    @RequestMapping("/")
    String main() {
        return "redirect:/joinGame";
    }

    @RequestMapping("/createGame")
    String createGame(Model model, @CookieValue("JSESSIONID") String cookie) {
        CreateGameInput createGameInput = new CreateGameInput(gameService.getTasks(
                folderScanService.scanFolder())
        );
        model.addAttribute("createGameInput", createGameInput);
//        System.out.println(createGameInput);
        return "createGame";
    }

    @RequestMapping(value = "/joinGame", method = RequestMethod.GET)
    String joinGame(Model model, @CookieValue("JSESSIONID") String cookie) {
        model.addAttribute("joinGameInput", new JoinGameInput());
        return "joinGame";
    }

    @RequestMapping(value = "/joinGame", method = RequestMethod.POST)
    String getTask(@ModelAttribute JoinGameInput joinGameInput, @CookieValue("JSESSIONID") String cookie, Model model) throws NoSuchUserException, SuchUserExistException, NoSuchGameException {
        gameService.addUser(joinGameInput.nick, joinGameInput.age, joinGameInput.groupId, cookie);
        return "redirect:/newtask";
    }

    @RequestMapping(value = "/newtask", method = RequestMethod.GET)
    String getNewTask(@CookieValue("JSESSIONID") String cookie, Model model) {
        return "redirect:/tasks/new";
    }


    @GetMapping("/tasks/new")
    public String getFile(@CookieValue("JSESSIONID") String cookie) throws NoSuchUserException {
        try {
            return gameService.getRandomTask(cookie) + "/index";
        } catch (NoMoreAvailableTasksException e) {
            return "redirect:/end";
        }
    }

    @RequestMapping("/manage")
    String manage(@ModelAttribute("createGameInput") CreateGameInputConfig createGameInputConfig, @CookieValue("JSESSIONID") String cookie, Model model) throws GameWithSuchIdExistException, NoGameSelectedException {
//        System.out.println(createGameInputConfig);

        List<String> userChoice = new ArrayList<>();
        if (createGameInputConfig.getTasksConfig() == null || createGameInputConfig.getTasksConfig().size() == 0) {
            throw new NoGameSelectedException("Nie wybrano żadnej gry");
        } else {
            userChoice = createGameInputConfig.getTasksConfig();
        }
        //todo
//        userChoice.forEach(System.out::println);
        String groupId = createGameInputConfig.getGroupId();
        if (groupId == null || groupId.trim().equals("")) {
            groupId = gameService.generateRandomGroupId();
        }
        gameService.addGame(new Game(groupId, userChoice.stream().map(tn -> gameService.getTaskConfig(tn)).collect(Collectors.toList()), cookie, createGameInputConfig.getTaskNumber()));
        model.addAttribute("group_id", groupId);
        return "manage";
    }

    @RequestMapping("/{task_name}/config")
    @ResponseBody
    String getConfig(@PathVariable(value = "task_name") final String taskName, @CookieValue("JSESSIONID") String cookie) throws NoSuchUserException {
        return gameService.getConfig(taskName, cookie);
    }


    @RequestMapping(value = "/{task_name}/end", method = RequestMethod.POST)
    @ResponseBody
    public String endGame(@PathVariable(value = "task_name") final String taskName, @RequestBody TaskResult taskResult, @CookieValue("JSESSIONID") String cookie, Model model) throws NoSuchGameException, NoSuchUserException {
//        System.out.println("/{task_name}/end -> result " + taskResult + " cookie = " + cookie);
        gameService.addResult(taskName, cookie, taskResult.getNick(), taskResult.getGroup(), taskResult.getResult());
        model.addAttribute("result", taskResult);
        model.addAttribute("taskName", taskName);
        return "/taskResult";
    }

    @RequestMapping(value = "/taskResult", method = RequestMethod.GET)
    String taskResult(@CookieValue("JSESSIONID") String cookie, Model model) {
        model.addAttribute("taskMark", new TaskMark());
        return "taskResult";
    }

    @RequestMapping(value = "/taskResult", method = RequestMethod.POST)
    String addMark(@ModelAttribute TaskMark taskMark, @CookieValue("JSESSIONID") String cookie, Model model) throws NoSuchUserException, SuchUserExistException, NoSuchGameException {
        markService.addMark(taskMark.getTaskName(), taskMark.getMark());
        return "redirect:/newtask";
    }

    @RequestMapping(value = "/end", method = RequestMethod.GET)
    String end(@CookieValue("JSESSIONID") String cookie, Model model) {
        model.addAttribute("result", "Udało Ci się skończyć wszystkie zadania. \nCałkowity rezultat = " + gameService.getLastResults(cookie).getScore());
        return "end";
    }

    @RequestMapping(value = "/{group_id}/remove", method = RequestMethod.GET)
    String endGame(@CookieValue("JSESSIONID") String cookie, @PathVariable(value = "group_id") final String groupId) throws CannotRemoveGameException {
        gameService.removeGame(groupId, cookie);
        return "redirect:/";
    }

    @RequestMapping("/{group_id}/results")
    @ResponseBody
    Map<String, Double> getResults(@PathVariable(value = "group_id") final String groupId, @CookieValue("JSESSIONID") String cookie) throws NoSuchUserException, NoSuchGameException, LackOfAccessException {
//        gameService.getResults(groupId, cookie).forEach((k, v) -> System.out.println(k + " : " + v));
        return gameService.getResults(groupId, cookie);
    }

    @RequestMapping("/last/results")
    @ResponseBody
    LastResultResponse getLastResults(@CookieValue("JSESSIONID") String cookie) throws NoSuchUserException {
        return gameService.getLastResults(cookie);
    }
}