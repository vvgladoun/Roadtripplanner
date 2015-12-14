package xyz.roadtripplanner.places;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.roadtripplanner.R;
import xyz.roadtripplanner.database.DAO.UserDAOImplementation;
import xyz.roadtripplanner.model.Comment;
import xyz.roadtripplanner.model.User;

/**
 * @author xyz
 */
public class CommentAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<Comment> mComments;;

    public CommentAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
        mComments  = objects;
    }


    @Override
    public int getCount() {
        return mComments == null ? 0 : mComments.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View element = convertView;
        CommenViewHolder viewHolder;

        if(element == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            element = inflater.inflate(R.layout.comment_layout, null);

            viewHolder = new CommenViewHolder();
            viewHolder.mCommentUsername = (TextView)element.findViewById(R.id.commentUsername);
            viewHolder.mCommentDate = (TextView)element.findViewById(R.id.commentDate);
            viewHolder.mCommentText = (TextView)element.findViewById(R.id.commentText);

            element.setTag(viewHolder);
        }
        else {
            viewHolder = (CommenViewHolder)element.getTag();
        }
        Log.d("COMMENTS", "elements in adapter: " + mComments.size());
        Log.d("COMMENTS", "position: " + position);

        Comment comment = mComments.get(position);

        User user = (new UserDAOImplementation(context)).findUserById(comment.getUserId());
        viewHolder.mCommentUsername.setText(user.getLogin());
        viewHolder.mCommentDate.setText(comment.getDate());
        viewHolder.mCommentText.setText(comment.getCommentText());

        return element;
    }

    class CommenViewHolder {
        TextView mCommentUsername;
        TextView mCommentDate;
        TextView mCommentText;
    }
}
