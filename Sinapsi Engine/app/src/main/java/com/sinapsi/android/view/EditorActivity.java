package com.sinapsi.android.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sinapsi.android.Lol;
import com.sinapsi.android.background.SinapsiActionBarActivity;
import com.sinapsi.engine.R;
import com.sinapsi.model.MacroInterface;
import com.sinapsi.model.impl.Macro;

public class EditorActivity extends SinapsiActionBarActivity {

    static int macroNameCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Object[] params = pullTempParameters();
        Lol.printNullity(this, "params", params);
        Lol.d(this, "params size: " + params.length);

        final MacroInterface input = (MacroInterface) params[0];

        final TextView tv = ((TextView) findViewById(R.id.test_text));
        tv.setText(input.getName());

        Button returnButton = (Button) findViewById(R.id.return_macro_button);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setName(tv.getText().toString());
                returnActivity(input);
                //TODO: check if macro is effectively changed
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
