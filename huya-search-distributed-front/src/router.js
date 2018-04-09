const routers = [
    {
        path: '/',
        meta: {
            title: ''
        },
        component: (resolve) => require(['./views/index.vue'], resolve)
    },
    {
        path: '/insertStat',
        meta: {
            title: ''
        },
        component: (resolve) => require(['./views/insertStat.vue'], resolve)
    }
];
export default routers;