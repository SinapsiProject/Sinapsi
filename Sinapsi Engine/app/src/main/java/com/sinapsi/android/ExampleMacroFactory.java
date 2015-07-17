package com.sinapsi.android;

import com.sinapsi.android.background.SinapsiBackgroundService;
import com.sinapsi.android.enginesystem.components.ActionToast;
import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.components.ActionContinueConfirmDialog;
import com.sinapsi.engine.components.ActionLog;
import com.sinapsi.engine.components.ActionSetVariable;
import com.sinapsi.engine.components.ActionSimpleNotification;
import com.sinapsi.engine.components.ActionStringInputDialog;
import com.sinapsi.engine.components.TriggerACPower;
import com.sinapsi.engine.components.TriggerSMS;
import com.sinapsi.engine.components.TriggerScreenPower;
import com.sinapsi.engine.parameters.ActualParamBuilder;
import com.sinapsi.model.MacroInterface;

/**
 * Created by Giuseppe on 14/07/15.
 */
public class ExampleMacroFactory {

    public static final int PC_LINUX_ID = 57;
    public static final int SINAPSI_CLOUD_ID = 51;

    public static MacroInterface example2(SinapsiBackgroundService service, MacroInterface m){


        m.setName("Screen-Var-Notif-LogServer-NotifDesktop");
        m.setTrigger(service.getComponentFactory().newTrigger(
                TriggerScreenPower.TRIGGER_SCREEN_POWER,
                new ActualParamBuilder()
                        .put("screen_power", true)
                        .create().toString(),
                m,
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionSetVariable.ACTION_SET_VARIABLE,
                new ActualParamBuilder()
                        .put("var_name", "var")
                        .put("var_scope", VariableManager.Scopes.LOCAL.toString())
                        .put("var_type", VariableManager.Types.STRING.toString())
                        .put("var_value", "prova")
                        .create().toString(),
                service.getDevice().getId()
        ));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Sinapsi macro test")
                        .put("notification_message", "Variable value: @{var}")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Variable value: @{var}")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionLog.ACTION_LOG,
                new ActualParamBuilder()
                        .put("log_message", "Variable value: @{var}")
                        .create().toString(),
                SINAPSI_CLOUD_ID));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Test Distributed Macro")
                        .put("notification_message", "The variable is: @{var}")
                        .create().toString(),
                PC_LINUX_ID));

        return m;
    }

    public static MacroInterface example1(SinapsiBackgroundService service, MacroInterface m){

        m.setName("Screen-ToastAndroid1-NotifAndroid2-NotifDesktop");
        m.setTrigger(service.getComponentFactory().newTrigger(
                TriggerScreenPower.TRIGGER_SCREEN_POWER,
                new ActualParamBuilder()
                        .put("screen_power", true)
                        .create().toString(),
                m,
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionToast.ACTION_TOAST,
                new ActualParamBuilder()
                        .put("message", "Screen is on.")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Sinapsi Macro")
                        .put("notification_message", "Screen is ON on Smartphone")
                        .create().toString(),
                54));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Sinapsi Macro")
                        .put("notification_message", "Screen is ON on Smartphone")
                        .create().toString(),
                PC_LINUX_ID));

        return m;
    }

    public static MacroInterface example3(SinapsiBackgroundService service, MacroInterface m){
        m.setName("ACPower-ConfirmDialog-Toast-NotifDesktop");
        m.setTrigger(service.getComponentFactory().newTrigger(
                TriggerACPower.TRIGGER_AC_POWER,
                new ActualParamBuilder()
                        .put("ac_power", false)
                        .create().toString(),
                m,
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionContinueConfirmDialog.ACTION_CONTINUE_CONFIRM_DIALOG,
                new ActualParamBuilder()
                        .put("dialog_title", "Continue?")
                        .put("dialog_message", "Are you sure you want to continue this macro?")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionToast.ACTION_TOAST,
                new ActualParamBuilder()
                        .put("message", "Macro continued.")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Test Distributed Macro")
                        .put("notification_message", "Wow.")
                        .create().toString(),
                PC_LINUX_ID));

        return m;
    }

    public static MacroInterface example4(SinapsiBackgroundService service, MacroInterface m){
        m.setName("SMSVariable-NotifDesktop");
        m.setTrigger(service.getComponentFactory().newTrigger(
                TriggerSMS.TRIGGER_SMS,
                new ActualParamBuilder()
                        .create().toString(),
                m,
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "SMS: @{sender_number}")
                        .put("notification_message", "@{message_content}")
                        .create().toString(),
                PC_LINUX_ID));

        return m;
    }

    public static MacroInterface example5(SinapsiBackgroundService service, MacroInterface m){
        m.setName("ACPower-InputString-NotifDesktop");
        m.setTrigger(service.getComponentFactory().newTrigger(
                TriggerACPower.TRIGGER_AC_POWER,
                new ActualParamBuilder()
                        .put("ac_power", false)
                        .create().toString(),
                m,
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionStringInputDialog.ACTION_STRING_INPUT_DIALOG,
                new ActualParamBuilder()
                        .put("dialog_title", "Write Something to send on PC.")
                        .put("dialog_message", "Write something.")
                        .put("var_name", "var")
                        .put("var_scope", VariableManager.Scopes.LOCAL.toString())
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionToast.ACTION_TOAST,
                new ActualParamBuilder()
                        .put("message", "Var value: @{var}")
                        .create().toString(),
                service.getDevice().getId()));
        m.addAction(service.getComponentFactory().newAction(
                ActionSimpleNotification.ACTION_SIMPLE_NOTIFICATION,
                new ActualParamBuilder()
                        .put("notification_title", "Test Input String Macro")
                        .put("notification_message", "Inserted string: @{var}")
                        .create().toString(),
                PC_LINUX_ID));
        return m;
    }

}
