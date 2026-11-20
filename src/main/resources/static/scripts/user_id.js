export let userIdPromise = fetch(`/api/get-user-id`).then((response)=> {
        if (response.ok)
            return response.text();
    })