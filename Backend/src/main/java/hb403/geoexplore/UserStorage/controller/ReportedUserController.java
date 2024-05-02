package hb403.geoexplore.UserStorage.controller;

import hb403.geoexplore.UserStorage.entity.ReportedUser;
import hb403.geoexplore.UserStorage.entity.User;
import hb403.geoexplore.UserStorage.repository.ReportedUserRepository;
import hb403.geoexplore.UserStorage.repository.UserRepository;
import hb403.geoexplore.comments.Entity.CommentEntity;
import io.swagger.v3.oas.annotations.Operation;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReportedUserController {

    @Autowired
    ReportedUserRepository reportedUserRepository;
    @Autowired
    UserRepository userRepository;

    

    //c of crudl
    @Operation(summary = "Add a new report of a user to the database")
    @PostMapping(path = "/user/report")
    public @ResponseBody ReportedUser ReportUser(@RequestBody ReportedUser newUser) {
        if (newUser == null ) {
            System.out.println("RequestBody null");
        }
        else if(!newUser.getHarassment() && !newUser.getSpamming() && !newUser.getMisinformation() && !newUser.getInappropriateContent()) {
            System.out.println("false report");
        }
        else {
            List<ReportedUser> getAllReportedUsers = reportedUserRepository.findAll();
            getAllReportedUsers.forEach(report -> {
                if (newUser.getReportedUserId().equals(report.getReportedUserId())) { //checks if user has already been reported to not make more reports than neccesary
                    report = addReport(report,newUser);
                    reportedUserRepository.save(report);
                }
            });
            newUser.setReportedUser(userRepository.findById(newUser.getReportedUserId()).get());
            User temp = userRepository.findById(newUser.getReportedUserId()).get();
            temp.setUser(newUser);
            reportedUserRepository.save(newUser);
            userRepository.save(temp);
            return newUser;
        }
        return newUser;
    }

    //adds report to a user that has already been reported without taking up more space.
    public ReportedUser addReport(ReportedUser report, ReportedUser newUser) {

        report.setNumReports(report.getNumReports() + 1);
        if (!report.getSpamming() && newUser.getSpamming()){
            report.setSpamming(true);
        }
        if (!report.getMisinformation() && newUser.getMisinformation()){
            report.setMisinformation(true);
        }
        if (!report.getInappropriateContent() && newUser.getInappropriateContent()){
            report.setInappropriateContent(true);
        }
        if (!report.getHarassment() && newUser.getHarassment()){
            report.setHarassment(true);
        }
        return report;
    }


    @Operation(summary = "Gets a reported user based on userId")
    @GetMapping(path = "/user/report/{id}")
    public @ResponseBody ReportedUser getReported(@PathVariable Long id){
        try{List<ReportedUser> getAllReportedUsers = reportedUserRepository.findAll();
            final ReportedUser[] temp = new ReportedUser[1];
        getAllReportedUsers.forEach(user -> {
            if(user.getReportedUserId().equals (id)){
                temp[0] = user;
            }
        });
        if (temp[0] != null) {
            return temp[0];
        }

        }catch (Exception e){
            System.out.println(e);
            throw e;
        }
        return null;
    }

    @Operation(summary = "Gets a reported user based on ReportedUserId")
    @GetMapping(path = "/user/report/{id}/mod")
    public @ResponseBody ReportedUser getReportedById(@PathVariable Long id){
        try{
            if(reportedUserRepository.findById(id).isPresent()) {
                System.out.println("ReportedUser form is present");
                return reportedUserRepository.findById(id).get();
            }
        }catch (Exception e){
            System.out.println(e);
            throw e;
        }
        return null;
    }

    @Operation(summary = "Updates a report on a user using the id of the report")
    @PutMapping(path = "user/report/update")
    public @ResponseBody ReportedUser updateReportedUser(@RequestBody ReportedUser updated){
        if (updated == null || updated.getReportedUserId() == null){

        }
        try {
            ReportedUser temp = reportedUserRepository.findById(updated.getId()).get();
            User tempUser = temp.getReportedUser();
            tempUser.setUser(temp);
            updated.setReportedUser(temp.getReportedUser());
            updated.setNumReports(temp.getNumReports());
            userRepository.save(tempUser);
            reportedUserRepository.save(updated);
        }
        catch (Exception e){
            throw e;
        }
        return null;
    }

    @Operation(summary = "deletes report but not user uses id of report not of user, basically if a user is innocent")
    @DeleteMapping(path = "user/report/deletereport/{id}")
    public @ResponseBody String deleteUserReport(@PathVariable Long id){
        try{
            ReportedUser temp_report =  reportedUserRepository.findById(id).get();
            User temp_user = userRepository.findById(temp_report.getReportedUserId()).get();
            temp_user.setUser(null);
            temp_report.setReportedUser(null);
            System.out.println("Nullifys connection in order to not delete the user");
            reportedUserRepository.deleteById(id);
        }catch (Exception e){
            throw e;
        }
        return "Success";
    }

    @Operation(summary = "deletes report and user uses id of report not of user, basically if a user is guilty or has been reported enough times")
    @DeleteMapping(path = "user/report/delete/{id}")
    public @ResponseBody String deleteUser(@PathVariable Long id){
        try{
            User temp = userRepository.findById(reportedUserRepository.findById(id).get().getReportedUserId()).get();

            if (userRepository.findById(temp.getId()).isPresent()){
                System.out.println("User exists and was found");
                System.out.println(temp.getRole());
                User user_to_ban = userRepository.findById(temp.getId()).get();
                user_to_ban.ban();
                System.out.println(user_to_ban.getRole());
                userRepository.save(user_to_ban);
                System.out.println(user_to_ban.getRole());
                ReportedUser tempReport = reportedUserRepository.findById(id).get();
                tempReport.setReportedUser(null);
                reportedUserRepository.deleteById(id);
            }
        }catch (Exception e){
            throw e;
        }
        return "Success, hopefully";
    }

    @Operation(summary = "Lists all userReports")
    @GetMapping(path = "user/report/list")
    public @ResponseBody List<ReportedUser> ListOfReports(){
        return reportedUserRepository.findAll();
    }
}
