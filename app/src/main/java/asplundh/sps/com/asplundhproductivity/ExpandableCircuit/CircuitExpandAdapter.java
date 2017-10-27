package asplundh.sps.com.asplundhproductivity.ExpandableCircuit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import java.util.List;

import asplundh.sps.com.asplundhproductivity.R;
import asplundh.sps.com.asplundhproductivity.Utils.AppConstants;

/**
 * Created by Malik Muhamad Qirtas on 10/25/2017.
 */

public class CircuitExpandAdapter extends ExpandableRecyclerAdapter<CircuitParentModel, CircuitChildModel, CircuitOuterViewHolder, CircuitInnerViewHolder>
{
    private static final int PARENT_VEGETARIAN = 0;
    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_VEGETARIAN = 2;
    private static final int CHILD_NORMAL = 3;
    
    private LayoutInflater mInflater;
    private List<CircuitParentModel> mRecipeList;
    Context mContext;
    
    public CircuitExpandAdapter(Context context, @NonNull List<CircuitParentModel> recipeList) {
        super(recipeList);
        mRecipeList = recipeList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    
    @UiThread
    @NonNull
    @Override
    public CircuitOuterViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView;
        
        int layout = R.layout.row_outer_circuit;
        
        switch (viewType) {
            default:
            case PARENT_NORMAL:
                recipeView = mInflater.inflate(layout, parentViewGroup, false);
                break;
            /*case PARENT_VEGETARIAN:
                recipeView = mInflater.inflate(R.layout.vegetarian_recipe_view, parentViewGroup, false);
                break;*/
        }
        return new CircuitOuterViewHolder(recipeView , mContext);
    }
    
    @UiThread
    @NonNull
    @Override
    public CircuitInnerViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView;
        switch (viewType) {
            default:
            case CHILD_NORMAL:
                ingredientView = mInflater.inflate(R.layout.row_inner_circuit, childViewGroup, false);
                break;
            case CHILD_VEGETARIAN:
                ingredientView = mInflater.inflate(R.layout.row_inner_circuit, childViewGroup, false);
                break;
        }
        return new CircuitInnerViewHolder(ingredientView);
    }
    
    
    @UiThread
    @Override
    public void onBindParentViewHolder(@NonNull CircuitOuterViewHolder recipeViewHolder, int parentPosition, @NonNull CircuitParentModel recipe) {
        recipeViewHolder.bind(recipe);
        
        // mRecipeList.get(parentPosition).;
        
        /*recipeViewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(AppConstants.TAG , "*************" );
            }
        });*/
        
        // recipeViewHolder.itemView.getParent().
        
        
        //  Log.i(AppConstants.TAG , "onBindParentViewHolder: "  + recipeViewHolder.getAdapterPosition());
        
        //   ImageView mArrowExpandImageView = (ImageView) itemView.findViewById(R.id.iv_Arrow);
    }
    
    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull CircuitInnerViewHolder ingredientViewHolder, int parentPosition, int childPosition, @NonNull CircuitChildModel ingredient) {
        ingredientViewHolder.bind(ingredient);
    }
    
    @Override
    public int getParentViewType(int parentPosition) {
        try
        {
            if (mRecipeList.get(parentPosition).isVegetarian()) {
                return PARENT_VEGETARIAN;
            } else {
                return PARENT_NORMAL;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            Log.e(AppConstants.TAG , "IndexOutOfBoundsException in getParentViewType CIRcuit expnad adapter: " + e.toString());
        }
        return PARENT_NORMAL;
    }
    
    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        CircuitChildModel ingredient = mRecipeList.get(parentPosition).getIngredient(childPosition);
        
        if(childPosition == 0)
            return CHILD_VEGETARIAN;
        else
            return CHILD_NORMAL;
      /*  if (ingredient.isVegetarian()) {
            return CHILD_VEGETARIAN;
        } else {
            return CHILD_NORMAL;
        }*/
    }
    
    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_VEGETARIAN || viewType == PARENT_NORMAL;
    }
    
}
