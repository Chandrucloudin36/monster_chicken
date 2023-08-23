import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _sharedValue = MutableLiveData<String>()
    val sharedValue: LiveData<String>
        get() = _sharedValue

    fun setSharedValue(newValue: String) {
        _sharedValue.value = newValue
    }

   /* private val _count = MutableLiveData<String>()
    private val _amount = MutableLiveData<String>()
    val sharedValue1: LiveData<String>
        get() = _count

    fun setCount(newValue: String) {
        _count.value = newValue
    }
    val sharedValue2: LiveData<String>
        get() = _amount

    fun setSharedValue(newValue: String) {
        _amount.value = newValue
    }*/
}
