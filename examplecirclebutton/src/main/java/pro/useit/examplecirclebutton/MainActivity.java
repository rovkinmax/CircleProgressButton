package pro.useit.examplecirclebutton;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.custom.button.CircleButton;


public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CircleButton circleButton = (CircleButton) findViewById(R.id.pieCircle);
        circleButton.setText("2");
        circleButton.setStateListener(new CircleButton.StateListener()
        {
            @Override
            public void onShowProgress()
            {
                new AsyncTask<Void, Void, Void>()
                {

                    @Override
                    protected Void doInBackground(final Void... params)
                    {
                        try
                        {
                            Thread.sleep(4000);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void p)
                    {
                        circleButton.hideProgress();
                    }
                }.execute();
            }

            @Override
            public void onReturnDefState()
            {
                Toast.makeText(getApplicationContext(), "default state", Toast.LENGTH_LONG).show();
            }
        });
    }
}
