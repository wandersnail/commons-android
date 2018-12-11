package cn.zfs.commonsdemo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.snail.commons.base.BaseHolder
import com.snail.commons.base.BaseListAdapter
import com.snail.commons.entity.Storage
import com.snail.commons.utils.FileUtils
import com.snail.commons.utils.SystemUtils
import kotlinx.android.synthetic.main.activity_storage.*


/**
 * 描述:
 * 时间: 2018/5/27 14:20
 * 作者: zengfansheng
 */
class StorageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        val storages = SystemUtils.getStorages(this)
        lv.adapter = ListAdapter(this, storages)
    }

    private inner class ListAdapter(context: Context, data: MutableList<Storage>) : BaseListAdapter<Storage>(context, data) {
        override fun getHolder(position: Int): BaseHolder<Storage> {
            return object : BaseHolder<Storage>() {
                private var tvPath: TextView? = null
                private var tvAvailaleSize: TextView? = null
                private var tvTotalSize: TextView? = null
                private var tvRemovable: TextView? = null
                private var tvAllowMassStorage: TextView? = null
                private var tvUsb: TextView? = null
                private var tvPrimary: TextView? = null
                private var tvDesc: TextView? = null
                private var tvState: TextView? = null

                override fun createConvertView(): View {
                    val view = View.inflate(context, R.layout.item_storage, null)
                    tvPath = view.findViewById(R.id.tvPath)
                    tvAvailaleSize = view.findViewById(R.id.tvAvailaleSize)
                    tvTotalSize = view.findViewById(R.id.tvTotalSize)
                    tvRemovable = view.findViewById(R.id.tvRemovable)
                    tvState = view.findViewById(R.id.tvState)
                    tvAllowMassStorage = view.findViewById(R.id.tvAllowMassStorage)
                    tvUsb = view.findViewById(R.id.tvUsb)
                    tvDesc = view.findViewById(R.id.tvDesc)
                    tvPrimary = view.findViewById(R.id.tvPrimary)
                    return view
                }

                override fun setData(data: Storage, position: Int) {
                    tvPath!!.text = data.path
                    tvDesc!!.text = data.description
                    tvAvailaleSize!!.text = FileUtils.formatFileSize(data.availaleSize)
                    tvTotalSize!!.text = FileUtils.formatFileSize(data.totalSize)
                    tvRemovable!!.text = data.isRemovable.toString()
                    tvPrimary!!.text = data.isPrimary.toString()
                    tvAllowMassStorage!!.text = data.isAllowMassStorage.toString()
                    tvUsb!!.text = data.isUsb.toString()
                    tvState!!.text = data.state
                }
            }
        }
    }
}