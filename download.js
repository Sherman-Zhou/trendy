export function downloadFileByGet (params, moduleName) {
  return axios.request({
    url: `/${moduleName}/download`,
    // header: { 'charset': 'utf-8' },
    // method: 'post',
    method: 'get',
    // data: paramters,
    params,
    responseType: 'blob'
  }).then(response => {
    // console.log('headers:', response.headers)
    let url = window.URL.createObjectURL(new Blob([response.data]))
    let link = document.createElement('a')
    link.style.display = 'none'
    link.href = url
    link.setAttribute('download', getFileName(response.headers['content-disposition']))
    document.body.appendChild(link)
    link.click()
  })
}

export function downloadFile (paramters, moduleName) {
  return axios.request({
    url: `/${moduleName}/download`,
    method: 'post',
    data: paramters,
    responseType: 'blob'
  }).then(response => {
    // console.log('headers:', response.headers)
    let url = window.URL.createObjectURL(new Blob([response.data]))
    let link = document.createElement('a')
    link.style.display = 'none'
    link.href = url
    link.setAttribute('download', getFileName(response.headers['content-disposition']))

    document.body.appendChild(link)
    link.click()
  })
}