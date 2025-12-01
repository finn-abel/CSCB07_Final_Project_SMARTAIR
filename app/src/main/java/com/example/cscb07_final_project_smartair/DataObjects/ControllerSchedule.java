package com.example.cscb07_final_project_smartair.DataObjects;

import com.google.firebase.auth.FirebaseAuth;
import java.util.List;
import java.util.Map;

public class ControllerSchedule extends Data {
    public Map<String, List<ScheduleEntry>> schedule;

    public ControllerSchedule() {
    }

    public ControllerSchedule(Map<String, List<ScheduleEntry>> schedule) {
        super(System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "controllerSchedule");
        this.schedule = schedule;
    }
}
