const HTTP = axios.create({
    baseURL: 'http://localhost:8888/api',
    timeout: 5 * 1000,
    withCredentials: true
    // headers: {
    //   'Content-type': 'application/json'
    // }
});

// 添加请求拦截器
HTTP.interceptors.request.use(config => {
    // if (window.localStorage.userToken) {
    //   config.headers.common['Authorization'] = 'Bearer' + ' ' + window.localStorage.getItem('userToken')
    // }
    if (config.method.toLowerCase() === 'post' || config.method.toLowerCase() === 'get') {
        if (config.data && !config.data.jsonFlag) {
            config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
        }
    }
    if (config.method.toLowerCase() === 'post') {
        let urlParams = '';
        let data = config.data;
        if (config.data && !config.data.jsonFlag) {
            for (let [k, v] of Object.entries(data)) {
                urlParams += k + '=' + v + '&';
            }
            config.data = urlParams.slice(0, -1);
        }
    }
    if (localStorage.userToken) {
        config.headers.common['userToken'] = localStorage.getItem('userToken');
    }
    console.log(config);
    return config;
}, error => {
    return Promise.reject(error);
});

// 添加响应拦截器
HTTP.interceptors.response.use(res => {
    return res.data;
}, reason => {
    return Promise.reject(reason);
});

function getJsonData() {
    return axios.get('../json/data.json');
}


function submitLogin(data) {
    return HTTP.request({
        url: '/user/login',
        method: 'post',
        data
    });
}


function dailyList(data) {
    return HTTP.request({
        url: '/daily/list-daily',
        method: 'get',
        params: data
    });
}

function searchDaily(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/daily/search-daily',
        method: 'post',
        data
    });
}

function getDaily(data) {
    return HTTP.request({
        url: '/daily/get-daily',
        method: 'get',
        params: data
    });
}

function insertDaily(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/daily/insert-daily',
        method: 'post',
        data
    });
}

function updateDaily(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/daily/update-daily',
        method: 'post',
        data
    });
}


function deleteDaily(data) {
    return HTTP.request({
        url: '/daily/delete-daily',
        method: 'post',
        data
    });
}


function userList(data) {
    return HTTP.request({
        url: '/user/list-user',
        method: 'get',
        params: data
    });
}

function searchUser(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/user/search-user',
        method: 'post',
        data
    });
}

function getUser(data) {
    return HTTP.request({
        url: '/user/get-user',
        method: 'get',
        params: data
    });
}

function insertUser(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/user/insert-user',
        method: 'post',
        data
    });
}

function updateUser(data) {
    data ? data.jsonFlag = true : '';
    return HTTP.request({
        url: '/user/update-user',
        method: 'post',
        data
    });
}


function deleteUser(data) {
    return HTTP.request({
        url: '/user/delete-user',
        method: 'post',
        data
    });
}


function totalDailyList(data) {
    return HTTP.request({
        url: '/daily/list-total-daily',
        method: 'get',
        params: data
    });
}
