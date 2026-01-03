function Component() {
    // JSX: JavaScript XML
    const name = "Cam";
    const loop = 5;
    const listItems = [];

    for (let i = 0; i < loop; i++) {
        listItems.push(<li>{i + 1}</li>);
    }

    return (
        <div>

            {name ? <h1>Hello {name}!!</h1> : <h1>Hello World!!</h1>}

            <ul>{listItems}</ul>

        </div>
    );

}

export default Component;