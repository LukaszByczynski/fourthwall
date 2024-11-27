# DefaultApi

All URIs are relative to *http://www.omdbapi.com*

| Method | HTTP request | Description |
| ------------- | ------------- | ------------- |
| [**getOMDbSearch**](DefaultApi.md#getOMDbSearch) | **GET** / | OMDb Search |


<a id="getOMDbSearch"></a>
# **getOMDbSearch**
> CombinedResult getOMDbSearch(r, t, i, s, y, type, plot, tomatoes, v, page, paramCallback)

OMDb Search

Find a movie, series or episode from the OMDb by title, IMDb-id or by free-text search

### Example
```kotlin
// Import classes:
//import com.omdbapi.client.infrastructure.*
//import com.omdbapi.client.models.*

val apiInstance = DefaultApi()
val r : kotlin.String = r_example // kotlin.String | The data type to return.
val t : kotlin.String = t_example // kotlin.String | Movie title to search for. One of t, i or s is required.
val i : kotlin.String = i_example // kotlin.String | A valid IMDb ID (e.g. tt1285016). One of t, i or s is required.
val s : kotlin.String = s_example // kotlin.String | Movie title to search for. One of t, i or s is required.
val y : kotlin.Int = 56 // kotlin.Int | Year of release.
val type : kotlin.String = type_example // kotlin.String | Type of result to return.
val plot : kotlin.String = plot_example // kotlin.String | Return short or full plot.
val tomatoes : kotlin.Boolean = true // kotlin.Boolean | Include Rotten Tomatoes ratings.
val v : kotlin.Int = 56 // kotlin.Int | API version (reserved for future use).
val page : kotlin.Int = 56 // kotlin.Int | Page number to return.
val paramCallback : kotlin.String = paramCallback_example // kotlin.String | JSONP callback name.
try {
    val result : CombinedResult = apiInstance.getOMDbSearch(r, t, i, s, y, type, plot, tomatoes, v, page, paramCallback)
    println(result)
} catch (e: ClientException) {
    println("4xx response calling DefaultApi#getOMDbSearch")
    e.printStackTrace()
} catch (e: ServerException) {
    println("5xx response calling DefaultApi#getOMDbSearch")
    e.printStackTrace()
}
```

### Parameters
| **r** | **kotlin.String**| The data type to return. | [default to json] [enum: json, xml] |
| **t** | **kotlin.String**| Movie title to search for. One of t, i or s is required. | [optional] |
| **i** | **kotlin.String**| A valid IMDb ID (e.g. tt1285016). One of t, i or s is required. | [optional] |
| **s** | **kotlin.String**| Movie title to search for. One of t, i or s is required. | [optional] |
| **y** | **kotlin.Int**| Year of release. | [optional] |
| **type** | **kotlin.String**| Type of result to return. | [optional] [enum: movie, series, episode] |
| **plot** | **kotlin.String**| Return short or full plot. | [optional] [default to short] [enum: short, full] |
| **tomatoes** | **kotlin.Boolean**| Include Rotten Tomatoes ratings. | [optional] [default to false] |
| **v** | **kotlin.Int**| API version (reserved for future use). | [optional] [default to 1] |
| **page** | **kotlin.Int**| Page number to return. | [optional] [default to 1] |
| Name | Type | Description  | Notes |
| ------------- | ------------- | ------------- | ------------- |
| **paramCallback** | **kotlin.String**| JSONP callback name. | [optional] |

### Return type

[**CombinedResult**](CombinedResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/xml

